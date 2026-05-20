(ns com.badlogic.gdx
  (:require [gdl.app :as app])
  (:import (com.badlogic.gdx Application
                             Gdx)))

(defn app []
  Gdx/app)

(extend-type Application
  app/App
  (audio [app]
    (.getAudio app))

  (files [app]
    (.getFiles app))

  (graphics [app]
    (.getGraphics app))

  (input [app]
    (.getInput app)))
