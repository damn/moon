(ns levelgen-test.dispose
  (:require [clojure.gdx :as gdx]))

(defn f!
  [{:keys [ctx/skin
           ctx/sprite-batch
           ctx/tiled-map]}]
  ; TODO TEXTURES NOT DISPOSED
  (gdx/dispose! skin)
  (gdx/dispose! sprite-batch)
  (gdx/dispose! tiled-map))
