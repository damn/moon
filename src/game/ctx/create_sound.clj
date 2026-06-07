(ns game.ctx.create-sound
  (:require [com.badlogic.gdx.audio :as audio]
            [com.badlogic.gdx.files :as files]))

(defn create-sound
  [{:keys [ctx/audio
           ctx/files]}
   path]
  (audio/new-sound audio
                   (files/internal files path)))
