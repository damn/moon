(ns clojure.ui.window.add-close-button
  (:require
            [clojure.scene2d.actor.add-listener]
            [clojure.scene2d.actor.remove-actor] [clojure.window :as window]
            [clojure.scene2d.utils.change-listener :as change-listener]
            [clojure.ui.table.add-cell :refer [add-cell!]]
            [clojure.ui-text-button :as text-button]))

(defn f! [window skin]
  (add-cell! (window/get-title-table window)
             {:actor (doto (text-button/create {:text "X" :skin skin})
                       (clojure.scene2d.actor.add-listener/f (change-listener/create
                                       (fn [_event _actor]
                                         (clojure.scene2d.actor.remove-actor/f window)))))}))
