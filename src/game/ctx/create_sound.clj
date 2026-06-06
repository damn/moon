(ns game.ctx.create-sound
  (:require [gdx.application :as app]
            [gdx.audio :as audio]
            [gdx.files :as files]))

(defn create-sound [{:keys [ctx/app]} path]
  (audio/new-sound (app/audio app)
                   (files/internal (app/files app) path)))
