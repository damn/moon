(ns com.badlogic.gdx
  (:require [gdl.app :as app]
            [gdl.audio :as audio]
            [gdl.files :as files])
  (:import (com.badlogic.gdx Application
                             Audio
                             Files
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

(extend-type Audio
  audio/Audio
  (new-sound [this file-handle]
    (.newSound this file-handle)))

(extend-type Files
  files/Files
  (internal [this path]
    (.internal this path)))
