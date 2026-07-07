(ns clojure.app-dispose
  (:require [clojure.disposable :as disposable]))

(defn dispose!
  [{:keys [ctx/skin
           ctx/batch
           ctx/textures]}]
  (disposable/dispose! batch)
  (disposable/dispose! skin)
  (run! disposable/dispose! (vals textures)))
