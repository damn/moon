(ns clojure.ui-scroll-pane
  (:require [gdl.scroll-pane :as scroll-pane]))

(defn create
  [{:keys [actor skin]}]
  (scroll-pane/new actor skin))
