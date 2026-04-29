(ns moon.application.create.textures
  (:require [moon.textures :as textures]))

(defn step [ctx]
  (assoc ctx :ctx/textures (textures/create (:ctx/files ctx)
                                            {:folder "resources/"
                                             :extensions #{"png" "bmp"}})))
