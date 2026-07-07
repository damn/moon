(ns gdx.scene2d.ui.window.add-close-button
  (:require [clojure.window :as window]
            [clojure.actor :as actor]
            [gdx.scene2d.utils.change-listener :as change-listener]
            [gdx.scene2d.ui.table.add-cell :refer [add-cell!]]
            [gdx.scene2d.ui.text-button :as text-button]))

(defn f! [window skin]
  (add-cell! (window/get-title-table window)
             {:actor (doto (text-button/create {:text "X" :skin skin})
                       (actor/add-listener! (change-listener/create
                                       (fn [_event _actor]
                                         (actor/remove! window)))))}))
