(ns moon.ui.window
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.window :as window]))

(defn create
  [{:keys [title skin]}]
  (window/create title skin))
