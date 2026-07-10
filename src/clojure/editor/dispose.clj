(ns clojure.editor.dispose
  (:require [gdl.utils.disposable :as disposable]))

(defn dispose [{:keys [ctx/skin
                             ctx/batch
                             ctx/textures]}]
  (disposable/dispose! batch)
  (disposable/dispose! skin)
  (run! disposable/dispose! (vals textures)))
