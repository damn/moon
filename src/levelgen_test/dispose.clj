(ns levelgen-test.dispose
  (:require [utils.dispose :as disposable]))

(defn f!
  [{:keys [ctx/skin
           ctx/sprite-batch
           ctx/tiled-map]}]
  ; TODO TEXTURES NOT DISPOSED
  (disposable/dispose! skin)
  (disposable/dispose! sprite-batch)
  (disposable/dispose! tiled-map))
