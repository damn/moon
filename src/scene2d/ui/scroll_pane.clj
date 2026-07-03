(ns scene2d.ui.scroll-pane
  (:require [clojure.gdx.scroll-pane.new :as new-scroll-pane]))

(defn create
  [{:keys [actor skin]}]
  (new-scroll-pane/f actor skin))
