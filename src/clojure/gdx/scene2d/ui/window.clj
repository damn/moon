(ns clojure.gdx.scene2d.ui.window
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.table :as gdx-table]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.text-button :as text-button]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.window :as window]
            [moon.actor :as actor]
            [moon.table :as table]))

(defn- add-close-button! [window skin]
  (gdx-table/add!
   (window/title-table window)
   (doto (text-button/create "X" skin)
     (actor/add-listener!
      [:listener/change (fn [_event _actor]
                          (actor/remove! window))]))))

(defn set-opts! [window opts]
  (when (:window/modal? opts)
    (window/set-modal! window true))
  (when-let [skin (:window/close-button? opts)]
    (add-close-button! window skin))
  (table/set-opts! window opts))

(defn create
  [{:keys [title skin] :as opts}]
  (doto (window/create title skin)
    (set-opts! opts)))
