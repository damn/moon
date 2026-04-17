(ns clojure.gdx.scene2d.ui.window
  (:require [clojure.gdx.scene2d.ui.text-button :as text-button]
            [clojure.scene2d.actor :as actor]
            [clojure.scene2d.ui.table :as table])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               Window)))

(defn- add-close-button! [window skin]
  (table/add!
   (Window/.getTitleTable window)
   {:actor (doto (text-button/create "X" skin)
             (actor/add-listener!
              [:listener/change (fn [_event _actor]
                                  (actor/remove! window))]))}))

(defn set-opts! [window opts]
  (when (:window/modal? opts)
    (Window/.setModal window true))
  (when-let [skin (:window/close-button? opts)]
    (add-close-button! window skin))
  (table/set-opts! window opts))

(defn create
  [{:keys [title skin] :as opts}]
  (doto (Window. ^String title ^Skin skin)
    (set-opts! opts)))
