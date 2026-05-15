(ns moon.dispose.batch
  (:require [clojure.gdx.utils.disposable :refer [dispose!]]))

(defn do!
  [{:keys [ctx/batch]}]
  (dispose! batch))
