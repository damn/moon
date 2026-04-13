(ns moon.ui.image
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.image :as image]
            [moon.ui.actor :as actor]))

(defn create
  [{:keys [texture-region] :as opts}]
  (doto (image/create texture-region)
    (actor/set-opts! opts)))
