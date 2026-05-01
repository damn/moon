(ns moon.application.create.textures
  (:require [moon.textures :as textures])
  (:import (com.badlogic.gdx Application)))

(defn step
  [{:keys [^Application ctx/app]
    :as ctx}]
  (assoc ctx :ctx/textures (textures/create (.getFiles app)
                                            {:folder "resources/"
                                             :extensions #{"png" "bmp"}})))
