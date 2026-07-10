(ns clojure.editor.create-widget-open-select-sounds-handler
  (:require [gdl.actor :as actor]
            [clojure.ui.window.add-close-button :as add-close-button]
            [clojure.ui.table.add-rows :refer [add-rows!]]
            [clojure.moon.ctx-do :refer [do!]]
            [clojure.editor.create-widget-rebuild-sound-widget :as rebuild-sound-widget]
            [gdl.event :as event]
            [clojure.scroll-pane-cell :as scroll-pane-cell]
            [clojure.stage :as stage]
            [clojure.ui-scroll-pane :as scroll-pane]
            [clojure.ui-table :as table]
            [clojure.ui-text-button :as text-button]
            [clojure.ui-window :as window]
            [clojure.scene2d.utils.change-listener :as change-listener]
            [clojure.viewport :as viewport]
            [clojure.window :as gdx-window]))

(defn open-select-sounds-handler [table ->sound-columns]
  (fn [{:keys [ctx/skin
               ctx/stage]
        :as ctx}]
    (stage/add-actor! stage
                      (doto (window/create
                             {:title "Choose"
                              :skin skin
                              :table/rows
                              [[(let [table (table/create
                                             {:table/cell-defaults {:pad 5}
                                              :table/rows (for [sound-name (map first (:ctx/audio ctx))]
                                                            [{:actor (doto (text-button/create
                                                                            {:text sound-name
                                                                             :skin skin})
                                                                           (actor/add-listener (change-listener/create
                                                                                                    (fn [event actor]
                                                                                                      ((rebuild-sound-widget/rebuild-sound-widget! table sound-name ->sound-columns) actor (:stage/ctx (event/get-stage event)))))))}
                                                             {:actor (doto (text-button/create
                                                                            {:text "play!"
                                                                             :skin skin})
                                                                           (actor/add-listener (change-listener/create
                                                                                                    (fn [event _actor]
                                                                                                      (do! (:stage/ctx (event/get-stage event))
                                                                                                           [[:tx/sound sound-name]])))))}])} )]
                                {:actor (scroll-pane/create
                                         {:actor table
                                          :skin skin})
                                 :width  (+ (actor/get-width table) 50)
                                 :height (min (- (viewport/get-world-height (:stage/viewport stage)) 50)
                                              (actor/get-height table))})]]})
                            (add-close-button/f! skin)
                            (gdx-window/set-modal! true)))))
