(ns scene2d.ui.scroll-pane
  (:require [com.badlogic.gdx.scenes.scene2d.ui.scroll-pane :as scroll-pane]))

(defn create
  [{:keys [actor skin]}]
  (new-scroll-pane/f actor skin))
