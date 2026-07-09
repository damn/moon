(ns clojure.editor.textures
  (:require [clojure.create-textures :as create-textures]))

(defn f [{:keys [ctx/files] :as ctx}]
  (assoc ctx :ctx/textures (create-textures/f files {:folder "resources/"
                                                      :extensions #{"png" "bmp"}})))
