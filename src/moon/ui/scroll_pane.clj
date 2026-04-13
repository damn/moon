(ns moon.ui.scroll-pane
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.scroll-pane :as scroll-pane]))

(defn create
  [{:keys [actor skin]}]
  (scroll-pane/create actor skin))
