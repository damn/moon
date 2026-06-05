(ns game.ctx.create-sound
  (:require [clojure.application :as app]
            [clojure.audio :as audio]
            [clojure.files :as files]))

(defn create-sound [{:keys [ctx/app]} path]
  (audio/new-sound (app/audio app)
                   (files/internal (app/files app) path)))
