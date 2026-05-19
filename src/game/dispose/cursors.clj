(ns game.dispose.cursors
  (:require [gdl.utils.disposable :refer [dispose!]]))

(defn do!
  [{:keys [ctx/cursors]}]
  (run! dispose! (vals cursors)))
