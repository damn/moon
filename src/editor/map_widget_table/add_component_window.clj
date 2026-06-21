(ns editor.map-widget-table.add-component-window
  (:require [clojure.event.get-stage :refer [get-stage]]
            [moon.schema.build-widget :as build-widget]
            [moon.schema.widget-value :as widget-value]
            [clojure.actor.remove :refer [remove!]]
            [clojure.actor.add-listener :refer [add-listener!]]
            [clojure.ui.table.add-rows :refer [add-rows!]]
            [clojure.ui.text-button :as text-button]
            [clojure.layout.pack :refer [pack!]]
            [clojure.change-listener :as change-listener]
            [clojure.window.add-close-button :as add-close-button]
            [clojure.window.set-modal :as set-modal]
            [gdx.scenes.scene2d.ui.window :as window]
            [moon.schemas.default-value :refer [default-value]]
            [moon.schemas.map-keys :refer [map-keys]]
            [moon.schemas.optional :refer [optional?]]))

(defn f
  [{:keys [schemas schema map-widget-table skin]}]
  (let [window (doto (window/create
                      {:title "Choose"
                       :skin skin
                       :table/cell-defaults {:pad 5}})
                 (add-close-button/f! skin)
                 (set-modal/f! true))
        remaining-ks (sort (remove (set (keys (widget-value/f schema map-widget-table schemas)))
                                   (map-keys schemas schema)))]
    (add-rows!
     window
     (for [k remaining-ks]
       [{:actor (doto (text-button/create
                       {:skin skin
                        :text (name k)})
                  (add-listener! (change-listener/create
                                  (fn [event _actor]
                                    (remove! window)
                                    (let [ctx (:stage/ctx (get-stage event))]
                                      (add-rows! map-widget-table [((:ctx/create-component-row ctx)
                                                                    {:skin skin
                                                                     :editor-widget (build-widget/f ctx
                                                                                                    (get schemas k)
                                                                                                    k
                                                                                                    (default-value schemas k))
                                                                     :k k
                                                                     :display-remove-component-button? (optional? schemas schema k)
                                                                     :table map-widget-table})])
                                      ((:ctx/rebuild-editor-window! ctx) ctx)))))
                  )}]))
    (pack! window)
    window))
