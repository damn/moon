(ns gdx.scenes.scene2d.ui.window
  (:require [clojure.actor.remove :refer [remove!]]
            [clojure.actor.add-listener :refer [add-listener!]]
            [clojure.ui.window :as window]
            [clojure.window.get-title-table :as get-title-table]
            [clojure.window.set-modal :as set-modal]
            [clojure.ui.table.add-cell :refer [add-cell!]]
            [clojure.ui.table.set-opts :refer [set-opts!]]
            [clojure.change-listener :as change-listener]
            [clojure.ui.text-button :as text-button]))

(defn create
  [{:keys [title skin] :as opts}]
  (let [window (window/create title skin)]
    (when (:window/modal? opts)
      (set-modal/f! window true))

    (when-let [skin (:window/close-button? opts)]
      (add-cell! (get-title-table/f window)
                 {:actor (doto (text-button/create {:text "X" :skin skin})
                           (add-listener! (change-listener/create
                                           (fn [_event _actor]
                                             (remove! window)))))}))

    (set-opts! window opts)
    window))
