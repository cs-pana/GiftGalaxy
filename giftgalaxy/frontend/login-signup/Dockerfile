#build phase
FROM node:20.17.0-alpine AS build

#set working directory in the container
WORKDIR /app

#copy of the package file and install dependencies
COPY package*.json ./
RUN npm install

#copy of the source code in the app 
COPY . . 

#building react app
RUN npm run build

#production phase
FROM nginx:alpine

#copy of the build files of React from the build image
COPY --from=build /app/build /usr/share/nginx/html

#port 80
EXPOSE 80

#nginx start
CMD ["nginx", "-g", "daemon off;"]


