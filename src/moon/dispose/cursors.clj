(ns moon.dispose.cursors
  (:require [clojure.gdx.utils.disposable :refer [dispose!]]))

(defn do!
  [{:keys [ctx/cursors]}]
  (run! dispose! (vals cursors)))
