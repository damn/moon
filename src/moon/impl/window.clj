(ns moon.impl.window
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.table :as table]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.text-button :as text-button]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.window :as window]
            [clj.api.com.badlogic.gdx.scenes.scene2d.utils.change-listener :as change-listener]
            [moon.actor :as actor]))

(defn add-close-button! [window skin]
  (table/add! (window/title-table window)
              (doto (text-button/create "X" skin)
                (actor/add-listener!
                 (change-listener/create
                  (fn [_event _actor]
                    (actor/remove! window))))))
  window)
