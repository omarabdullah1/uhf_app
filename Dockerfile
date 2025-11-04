# UHF RFID System Docker Configuration
FROM openjdk:11-jre-slim

# Install required packages
RUN apt-get update && apt-get install -y \
    curl \
    nodejs \
    npm \
    && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Create application user
RUN groupadd -r rfid && useradd -r -g rfid rfid

# Copy Java application and dependencies
COPY build/libs/my-uhf-app.jar /app/
COPY libs/ /app/libs/
COPY native/ /app/native/

# Copy Node.js server
COPY rfid-server/package*.json /app/server/
COPY rfid-server/server.js /app/server/

# Copy React client build
COPY rfid-client/build/ /app/client/

# Copy configuration files
COPY config/ /app/config/
COPY .env.production /app/.env

# Install Node.js dependencies
WORKDIR /app/server
RUN npm ci --only=production

# Create necessary directories
RUN mkdir -p /app/logs /app/data /app/temp /app/pids \
    && chown -R rfid:rfid /app

# Set environment variables
ENV NODE_ENV=production
ENV JAVA_OPTS="-Xmx2048m -Xms1024m"
ENV JAVA_LIBRARY_PATH="/app/native:/app/libs"

# Expose ports
EXPOSE 5000

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:5000/api/health || exit 1

# Switch to application user
USER rfid

# Start the application
WORKDIR /app
CMD ["node", "server/server.js"]