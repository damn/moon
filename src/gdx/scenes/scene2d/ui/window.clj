(ns gdx.scenes.scene2d.ui.window
  (:require [clojure.gdx.scene2d.ui.window :as window]
            [clojure.gdx.scene2d.actor.remove :refer [remove!]]
            [gdx.scenes.scene2d.ui.table :as table]
            [gdx.scenes.scene2d.ui.text-button :as text-button]))

(defn create
  [{:keys [title skin] :as opts}]
  (let [window (window/create title skin)]
    (when (:window/modal? opts)
      (window/set-modal! window))

    (when-let [skin (:window/close-button? opts)]
      (table/add! (window/title-table window)
                  {:actor (text-button/create
                           {:text "X"
                            :skin skin
                            :actor/listeners {:listener/change (fn [_event _actor]
                                                                 (remove! window))}})}))

    (table/set-opts! window opts)
    window))
