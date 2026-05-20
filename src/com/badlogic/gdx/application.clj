(ns com.badlogic.gdx.application
  (:require [gdl.app :as app])
  (:import (com.badlogic.gdx Application)))

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
