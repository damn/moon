(ns editor.map-widget-table.add-component-window
  (:require [scene2d.event.get-stage :as get-stage]
            [moon.schema.build-widget :as build-widget]
            [moon.schema.widget-value :as widget-value]
            [scene2d.actor.remove :refer [remove!]]
            [scene2d.actor.add-listener :refer [add-listener!]]
            [scene2d.ui.table.add-rows :refer [add-rows!]]
            [scene2d.ui.text-button :as text-button]
            [scene2d.layout.pack :refer [pack!]]
            [scene2d.change-listener :as change-listener]
            [scene2d.ui.window.add-close-button :as add-close-button]
            [scene2d.ui.window.set-modal :as set-modal]
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
                                    (let [ctx (:stage/ctx (get-stage/f event))]
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
