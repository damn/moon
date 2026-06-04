(ns game.ctx.create-sound
  (:require [clojure.gdx.application :as app]
            [clojure.gdx.audio :as audio]
            [clojure.gdx.files :as files]))

(defn create-sound [{:keys [ctx/app]} path]
  (audio/new-sound (app/audio app)
                   (files/internal (app/files app) path)))
