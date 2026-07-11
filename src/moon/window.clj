(ns moon.window
  (:require [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [com.badlogic.gdx.scenes.scene2d.ui.label :as label]
            [com.badlogic.gdx.scenes.scene2d.ui.text-button :as text-button]
            [com.badlogic.gdx.scenes.scene2d.ui.window :as window]
            [com.badlogic.gdx.scenes.scene2d.utils.change-listener :as change-listener]
            [moon.table :refer [add-cell!]]))

(defn title-bar? [actor]
  (when (instance? label/class actor)
    (when-let [p (actor/getParent actor)]
      (when-let [p (actor/getParent p)]
        (and (instance? window/class p)
             (= (window/getTitleLabel p) actor))))))

(defn add-close-button! [window skin]
  (add-cell! (window/getTitleTable window)
             {:actor (doto (text-button/new "X" skin)
                       (actor/addListener (change-listener/create
                                           (fn [_event _actor]
                                             (actor/remove window)))))}))
