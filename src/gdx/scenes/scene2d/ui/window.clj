(ns gdx.scenes.scene2d.ui.window
  (:require [com.badlogic.gdx.scenes.scene2d.actor.remove :refer [remove!]]
            [com.badlogic.gdx.scenes.scene2d.actor.add-listener :refer [add-listener!]]
            [com.badlogic.gdx.scenes.scene2d.ui.window :as window]
            [com.badlogic.gdx.scenes.scene2d.ui.table.add-cell :refer [add-cell!]]
            [com.badlogic.gdx.scenes.scene2d.ui.table.set-opts :refer [set-opts!]]
            [com.badlogic.gdx.scenes.scene2d.utils.change-listener :as change-listener]
            [com.badlogic.gdx.scenes.scene2d.ui.text-button :as text-button]))

(defn create
  [{:keys [title skin] :as opts}]
  (let [window (window/create title skin)]
    (when (:window/modal? opts)
      (window/set-modal! window))

    (when-let [skin (:window/close-button? opts)]
      (add-cell! (window/title-table window)
                 {:actor (doto (text-button/create {:text "X" :skin skin})
                           (add-listener! (change-listener/create
                                           (fn [_event _actor]
                                             (remove! window)))))}))

    (set-opts! window opts)
    window))
