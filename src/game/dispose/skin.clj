(ns game.dispose.skin
  (:require [gdl.utils.disposable :refer [dispose!]]))

(defn do!
  [{:keys [ctx/skin]}]
  (dispose! skin))
