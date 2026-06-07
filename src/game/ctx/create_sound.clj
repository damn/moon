(ns game.ctx.create-sound
  (:require [com.badlogic.gdx.application :as app]
            [com.badlogic.gdx.audio :as audio]
            [com.badlogic.gdx.files :as files]))

(defn create-sound [{:keys [ctx/app]} path]
  (audio/new-sound (app/audio app)
                   (files/internal (app/files app) path)))
