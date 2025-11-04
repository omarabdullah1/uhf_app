#!/bin/bash

# Production Deployment Script for UHF RFID System
# This script handles the complete deployment process

set -e  # Exit on any error

echo "🚀 UHF RFID System Production Deployment"

# Configuration
APP_NAME="rfid-system"
APP_USER="rfid"
APP_GROUP="rfid"
APP_HOME="/opt/rfid-system"
CONFIG_DIR="/etc/rfid-system"
LOG_DIR="/var/log/rfid-system"
SERVICE_NAME="rfid-system"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Function to check if running as root
check_root() {
    if [ "$EUID" -ne 0 ]; then
        echo -e "${RED}❌ This script must be run as root${NC}"
        exit 1
    fi
}

# Function to create system user
create_user() {
    echo -e "${BLUE}👤 Creating system user and group...${NC}"
    
    # Create group if it doesn't exist
    if ! getent group $APP_GROUP > /dev/null 2>&1; then
        groupadd --system $APP_GROUP
        echo -e "${GREEN}✅ Created group: $APP_GROUP${NC}"
    else
        echo -e "${YELLOW}⚠️  Group $APP_GROUP already exists${NC}"
    fi
    
    # Create user if it doesn't exist
    if ! getent passwd $APP_USER > /dev/null 2>&1; then
        useradd --system --gid $APP_GROUP --home-dir $APP_HOME --no-create-home --shell /bin/false $APP_USER
        echo -e "${GREEN}✅ Created user: $APP_USER${NC}"
    else
        echo -e "${YELLOW}⚠️  User $APP_USER already exists${NC}"
    fi
}

# Function to create directories
create_directories() {
    echo -e "${BLUE}📁 Creating application directories...${NC}"
    
    # Create main directories
    mkdir -p $APP_HOME/{bin,lib,native,config,data,temp}
    mkdir -p $CONFIG_DIR
    mkdir -p $LOG_DIR
    mkdir -p /var/run/$APP_NAME
    
    # Set ownership
    chown -R $APP_USER:$APP_GROUP $APP_HOME
    chown -R $APP_USER:$APP_GROUP $LOG_DIR
    chown -R $APP_USER:$APP_GROUP /var/run/$APP_NAME
    chown root:$APP_GROUP $CONFIG_DIR
    
    # Set permissions
    chmod 755 $APP_HOME
    chmod 750 $CONFIG_DIR
    chmod 755 $LOG_DIR
    chmod 755 /var/run/$APP_NAME
    
    echo -e "${GREEN}✅ Directories created and configured${NC}"
}

# Function to install system dependencies
install_dependencies() {
    echo -e "${BLUE}📦 Installing system dependencies...${NC}"
    
    # Update package list
    apt-get update
    
    # Install required packages
    apt-get install -y \
        openjdk-11-jre \
        nodejs \
        npm \
        curl \
        wget \
        nginx \
        postgresql-client \
        redis-tools \
        logrotate \
        rsyslog
    
    echo -e "${GREEN}✅ System dependencies installed${NC}"
}

# Function to deploy application files
deploy_application() {
    echo -e "${BLUE}🎯 Deploying application files...${NC}"
    
    # Stop service if running
    if systemctl is-active --quiet $SERVICE_NAME; then
        echo -e "${BLUE}🔄 Stopping existing service...${NC}"
        systemctl stop $SERVICE_NAME
    fi
    
    # Copy application files
    echo -e "${BLUE}📋 Copying application files...${NC}"
    cp -r build/libs/* $APP_HOME/lib/
    cp -r libs/* $APP_HOME/lib/
    cp -r native/* $APP_HOME/native/
    cp -r rfid-server/* $APP_HOME/bin/
    
    # Copy configuration files
    cp config/app.conf $CONFIG_DIR/
    cp .env.production $CONFIG_DIR/environment
    
    # Set permissions
    chown -R $APP_USER:$APP_GROUP $APP_HOME
    chmod 640 $CONFIG_DIR/environment
    chmod 644 $CONFIG_DIR/app.conf
    
    echo -e "${GREEN}✅ Application files deployed${NC}"
}

# Function to install Node.js dependencies
install_node_dependencies() {
    echo -e "${BLUE}📦 Installing Node.js dependencies...${NC}"
    
    cd $APP_HOME/bin
    
    # Install production dependencies only
    sudo -u $APP_USER npm ci --only=production
    
    echo -e "${GREEN}✅ Node.js dependencies installed${NC}"
}

# Function to install systemd service
install_service() {
    echo -e "${BLUE}⚙️  Installing systemd service...${NC}"
    
    # Copy service file
    cp deployment/systemd/rfid-system.service /etc/systemd/system/
    
    # Reload systemd daemon
    systemctl daemon-reload
    
    # Enable service
    systemctl enable $SERVICE_NAME
    
    echo -e "${GREEN}✅ Systemd service installed and enabled${NC}"
}

# Function to configure nginx
configure_nginx() {
    echo -e "${BLUE}🌐 Configuring Nginx...${NC}"
    
    # Create nginx configuration
    cat > /etc/nginx/sites-available/$APP_NAME << EOF
server {
    listen 80;
    server_name _;

    # API proxy
    location /api/ {
        proxy_pass http://localhost:5000/api/;
        proxy_http_version 1.1;
        proxy_set_header Upgrade \$http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
        proxy_cache_bypass \$http_upgrade;
    }

    # Static files
    location / {
        root /var/www/$APP_NAME;
        try_files \$uri \$uri/ /index.html;
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    # Health check
    location /health {
        proxy_pass http://localhost:5000/api/health;
    }
}
EOF

    # Enable site
    ln -sf /etc/nginx/sites-available/$APP_NAME /etc/nginx/sites-enabled/
    
    # Test nginx configuration
    nginx -t
    
    # Reload nginx
    systemctl reload nginx
    
    echo -e "${GREEN}✅ Nginx configured${NC}"
}

# Function to configure logging
configure_logging() {
    echo -e "${BLUE}📝 Configuring logging...${NC}"
    
    # Create logrotate configuration
    cat > /etc/logrotate.d/$APP_NAME << EOF
$LOG_DIR/*.log {
    daily
    missingok
    rotate 30
    compress
    delaycompress
    notifempty
    create 644 $APP_USER $APP_GROUP
    postrotate
        systemctl reload $SERVICE_NAME > /dev/null 2>&1 || true
    endscript
}
EOF

    # Create rsyslog configuration
    cat > /etc/rsyslog.d/50-$APP_NAME.conf << EOF
# UHF RFID System logging
if \$programname == '$APP_NAME' then $LOG_DIR/system.log
& stop
EOF

    # Restart rsyslog
    systemctl restart rsyslog
    
    echo -e "${GREEN}✅ Logging configured${NC}"
}

# Function to start services
start_services() {
    echo -e "${BLUE}🚀 Starting services...${NC}"
    
    # Start and enable required services
    systemctl start postgresql || echo -e "${YELLOW}⚠️  PostgreSQL not installed or configured${NC}"
    systemctl start redis-server || echo -e "${YELLOW}⚠️  Redis not installed or configured${NC}"
    systemctl start nginx
    
    # Start application service
    systemctl start $SERVICE_NAME
    
    # Wait for service to start
    sleep 10
    
    # Check service status
    if systemctl is-active --quiet $SERVICE_NAME; then
        echo -e "${GREEN}✅ Service started successfully${NC}"
    else
        echo -e "${RED}❌ Service failed to start${NC}"
        systemctl status $SERVICE_NAME --no-pager
        exit 1
    fi
}

# Function to run post-deployment tests
run_tests() {
    echo -e "${BLUE}🧪 Running post-deployment tests...${NC}"
    
    # Test API health endpoint
    if curl -f http://localhost:5000/api/health > /dev/null 2>&1; then
        echo -e "${GREEN}✅ API health check passed${NC}"
    else
        echo -e "${RED}❌ API health check failed${NC}"
        return 1
    fi
    
    # Test nginx proxy
    if curl -f http://localhost/api/health > /dev/null 2>&1; then
        echo -e "${GREEN}✅ Nginx proxy test passed${NC}"
    else
        echo -e "${RED}❌ Nginx proxy test failed${NC}"
        return 1
    fi
    
    echo -e "${GREEN}✅ All tests passed${NC}"
}

# Function to show deployment summary
show_summary() {
    echo -e "\n${BLUE}📊 Deployment Summary${NC}"
    echo "====================="
    echo -e "Application: ${GREEN}$APP_NAME${NC}"
    echo -e "User/Group: ${GREEN}$APP_USER:$APP_GROUP${NC}"
    echo -e "Home Directory: ${GREEN}$APP_HOME${NC}"
    echo -e "Config Directory: ${GREEN}$CONFIG_DIR${NC}"
    echo -e "Log Directory: ${GREEN}$LOG_DIR${NC}"
    echo -e "Service Status: ${GREEN}$(systemctl is-active $SERVICE_NAME)${NC}"
    
    echo -e "\n${BLUE}🌐 Service URLs:${NC}"
    echo "====================="
    echo -e "API: ${GREEN}http://localhost:5000${NC}"
    echo -e "Web Interface: ${GREEN}http://localhost${NC}"
    echo -e "Health Check: ${GREEN}http://localhost/health${NC}"
    
    echo -e "\n${BLUE}🔧 Management Commands:${NC}"
    echo "====================="
    echo -e "Start service: ${YELLOW}systemctl start $SERVICE_NAME${NC}"
    echo -e "Stop service: ${YELLOW}systemctl stop $SERVICE_NAME${NC}"
    echo -e "Restart service: ${YELLOW}systemctl restart $SERVICE_NAME${NC}"
    echo -e "Check status: ${YELLOW}systemctl status $SERVICE_NAME${NC}"
    echo -e "View logs: ${YELLOW}journalctl -u $SERVICE_NAME -f${NC}"
    echo -e "View application logs: ${YELLOW}tail -f $LOG_DIR/*.log${NC}"
}

# Main deployment function
main() {
    echo -e "${BLUE}🎯 UHF RFID System Production Deployment${NC}"
    echo "==========================================="
    
    # Check prerequisites
    check_root
    
    # Run deployment steps
    create_user
    create_directories
    install_dependencies
    deploy_application
    install_node_dependencies
    install_service
    configure_nginx
    configure_logging
    start_services
    
    # Run tests
    if run_tests; then
        echo -e "\n${GREEN}🎉 Deployment completed successfully!${NC}"
        show_summary
        exit 0
    else
        echo -e "\n${RED}❌ Deployment completed with errors${NC}"
        echo -e "${YELLOW}Check service logs for details: journalctl -u $SERVICE_NAME${NC}"
        exit 1
    fi
}

# Handle Ctrl+C gracefully
trap 'echo -e "\n${YELLOW}Deployment interrupted!${NC}"; exit 1' INT

# Check for help flag
if [[ "$1" == "--help" || "$1" == "-h" ]]; then
    echo -e "${BLUE}Usage: $0 [options]${NC}"
    echo ""
    echo "Options:"
    echo "  --help, -h    Show this help message"
    echo ""
    echo "This script will:"
    echo "  1. Create system user and directories"
    echo "  2. Install system dependencies"
    echo "  3. Deploy application files"
    echo "  4. Install and configure systemd service"
    echo "  5. Configure Nginx reverse proxy"
    echo "  6. Set up logging and rotation"
    echo "  7. Start services and run tests"
    echo ""
    echo "Prerequisites:"
    echo "  - Run as root"
    echo "  - Ubuntu/Debian-based system"
    echo "  - Built application (./gradlew build)"
    echo "  - PostgreSQL and Redis (optional)"
    exit 0
fi

# Run main function
main