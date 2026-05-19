(ns game.dispose.default-font
  (:require [gdl.utils.disposable :refer [dispose!]]))

(defn do!
  [{:keys [ctx/default-font]}]
  (dispose! default-font))
