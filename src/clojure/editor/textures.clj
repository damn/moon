(ns clojure.editor.textures
  (:require [clojure.ctx-textures :as ctx-textures]))

(defn f [ctx]
  (assoc ctx :ctx/textures (ctx-textures/step ctx {:folder "resources/"
                                                   :extensions #{"png" "bmp"}})))
