(ns gdx.scenes.scene2d.ui.window
  (:require [clojure.gdx.scene2d.ui.window :as window]
            [clojure.gdx.scene2d.actor.remove :refer [remove!]]
            [clojure.gdx.scene2d.ui.table.add :refer [add!]]
            [clojure.gdx.scene2d.ui.table.set-opts :refer [set-opts!]]
            [clojure.gdx.scene2d.utils.change-listener :as change-listener]
            [clojure.gdx.scene2d.ui.text-button :as text-button]))

(defn create
  [{:keys [title skin] :as opts}]
  (let [window (window/create title skin)]
    (when (:window/modal? opts)
      (window/set-modal! window))

    (when-let [skin (:window/close-button? opts)]
      (add! (window/title-table window)
            {:actor (text-button/create
                     {:text "X"
                      :skin skin
                      :actor/listeners [(change-listener/create
                                         (fn [_event _actor]
                                           (remove! window)))]})}))

    (set-opts! window opts)
    window))
