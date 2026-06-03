(ns gdx.scenes.scene2d.ui.image-button
  (:require [clojure.gdx.scene2d.ui.image-button :as image-button]))

(defn create
  [{:keys [drawable]}]
  (image-button/create drawable))
