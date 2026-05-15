(ns moon.dispose.skin
  (:require [clojure.gdx.utils.disposable :refer [dispose!]]))

(defn do!
  [{:keys [ctx/skin]}]
  (dispose! skin))
