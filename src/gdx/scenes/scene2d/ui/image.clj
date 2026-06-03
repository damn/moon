(ns gdx.scenes.scene2d.ui.image
  (:require [clojure.gdx.scene2d.ui.image :as image]))

(defn create
  [{:keys [content]}]
  (image/create content))

(defn set-drawable! [image drawable]
  (image/set-drawable! image drawable))
