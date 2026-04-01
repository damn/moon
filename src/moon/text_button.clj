(ns moon.text-button
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.event :as event]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.text-button :as text-button]
            [clj.api.com.badlogic.gdx.scenes.scene2d.utils.change-listener :as change-listener]
            [moon.actor :as actor]
            [moon.stage :as stage]))

(defn create
  [{:keys [text on-clicked skin]}]
  (doto (text-button/create (str text) skin)
    (actor/add-listener!
     (change-listener/create
       (fn [event actor]
         (on-clicked actor (stage/ctx (event/stage event))))))))
