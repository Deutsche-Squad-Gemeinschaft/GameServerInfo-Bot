FROM openjdk:20-slim-bullseye

# Create bot user
ARG GUID=1000
ARG UUID=1000

RUN groupadd --force -g $GUID bot
RUN useradd -ms /bin/bash --no-user-group -g $GUID -u $UUID bot

# Configure the start script
COPY start-container /usr/local/bin/start-container
RUN chmod +x /usr/local/bin/start-container

# Configure the supervisor
COPY supervisord.conf /etc/supervisor/conf.d/supervisord.conf

# Add sources and change permissions
ADD ./ /bot
RUN chown -R bot:bot /bot


ENTRYPOINT ["start-container"]
