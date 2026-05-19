(ns game.dispose.batch
  (:require [gdl.utils.disposable :refer [dispose!]]))

(defn do!
  [{:keys [ctx/batch]}]
  (dispose! batch))
