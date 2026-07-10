(ns clojure.levelgen-test.dispose
  (:require [gdl.disposable :as disposable]))

(defn dispose
  [{:keys [ctx/skin
           ctx/sprite-batch
           ctx/textures
           ctx/tiled-map]}]
  (disposable/dispose! sprite-batch)
  (disposable/dispose! skin)
  (run! disposable/dispose! (vals textures))
  (disposable/dispose! tiled-map))
