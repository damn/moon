(ns clj.api.com.badlogic.gdx.backends.lwjgl3.application
  (:require [moon.utils :as utils])
  (:import (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application)))

(defn create [{:keys [listener config]}]
  (Lwjgl3Application. (utils/apply* listener)
                      (utils/apply* config)))
