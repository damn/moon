(ns clojure.ui-scroll-pane
  (:require [clojure.scroll-pane :as scroll-pane]))

(defn create
  [{:keys [actor skin]}]
  (scroll-pane/new actor skin))
