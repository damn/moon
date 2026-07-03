(ns levelgen-test.dispose
  (:require [clojure.gdx.disposable.dispose :as dispose]))

(defn f
  [{:keys [ctx/skin
           ctx/sprite-batch
           ctx/tiled-map]}]
  ; TODO TEXTURES NOT DISPOSED
  (dispose/f skin)
  (dispose/f sprite-batch)
  (dispose/f tiled-map))
