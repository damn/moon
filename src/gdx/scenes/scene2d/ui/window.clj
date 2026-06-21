(ns gdx.scenes.scene2d.ui.window
  (:require [clojure.actor.remove :refer [remove!]]
            [clojure.actor.add-listener :refer [add-listener!]]
            [clojure.ui.window :as window]
            [clojure.ui.table.add-cell :refer [add-cell!]]
            [clojure.ui.table.set-opts :refer [set-opts!]]
            [clojure.change-listener :as change-listener]
            [clojure.ui.text-button :as text-button]))

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
