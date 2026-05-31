(ns editor.window
  (:require [gdx.app :as app]
            [com.badlogic.gdx.input.keys :as input.keys]
            [gdx.scenes.scene2d.actor :as actor]
            [com.badlogic.gdx.scenes.scene2d.event :as event]
            [gdx.stage :as stage]
            [gdx.scenes.scene2d.ui :as ui]
            [com.badlogic.gdx.scenes.scene2d.ui.scroll-pane :as scroll-pane]
            [gdx.scenes.scene2d.ui.table :as table]
            [gdx.scenes.scene2d.ui.text-button :as text-button]
            [gdx.scenes.scene2d.ui.window :as window]
            [editor.widget :as widget]
            [moon.db :as db]
            [moon.property :as property]
            [moon.throwable :as throwable]
            [moon.ui.error-window :as error-window]))

(defn property-editor-window
  [{:keys [ctx
           property]}]
  (let [{:keys [ctx/db
                ctx/skin
                ctx/stage]} ctx
        schemas (:db/schemas db)
        schema (get schemas (property/type property))
        ; build for get-widget-value
        ; or find a way to find the widget from the context @ save button
        ; should be possible
        widget (widget/create schema property ctx) ; FIXME here no set user object k v ?
        scroll-pane-height (:viewport/world-height (:stage/viewport stage))
        get-widget-value #(widget/value schema widget schemas)
        property-id (:property/id property)
        with-window-close (fn [f]
                            (fn [actor {:keys [ctx/skin
                                               ctx/stage]
                                        :as ctx}]
                              (try
                               (let [new-ctx (update ctx :ctx/db f)
                                     stage (actor/stage actor)]
                                 (stage/set-ctx! stage new-ctx))
                               (actor/remove! (actor/find-ancestor actor ui/window?))
                               (catch Throwable t
                                 (throwable/pretty-pst t)
                                 (stage/add-actor! stage
                                                   (error-window/create
                                                    {:type :ui/error-window
                                                     :skin skin
                                                     :throwable t}))))))
        clicked-delete-fn (with-window-close (fn [db]
                                               (db/delete! db property-id)))
        clicked-save-fn (with-window-close (fn [db]
                                             (db/update! db (get-widget-value))))
        actors [(actor/create
                 {:act! (fn [this delta]
                          (when-let [stage (actor/stage this)]
                            (let [ctx (:stage/ctx stage)]
                              (when (app/key-just-pressed? (:ctx/app ctx) input.keys/enter)
                                (clicked-save-fn this ctx)))))})]
        save-button {:text "Save [LIGHT_GRAY](ENTER)[]"
                     :skin skin
                     :actor/listeners [[:listener/change
                                        (fn [event actor]
                                          (clicked-save-fn actor (:stage/ctx (event/stage event))))]]}
        delete-button {:text "Delete"
                       :skin skin
                       :actor/listeners [[:listener/change
                                          (fn [event actor]
                                            (clicked-delete-fn actor (:stage/ctx (event/stage event))))]]}
        scroll-pane-rows [[{:actor widget :colspan 2}]
                          [{:actor (text-button/create save-button) :center? true}
                           {:actor (text-button/create delete-button) :center? true}]]
        rows [[(let [table (table/create
                            {:table/cell-defaults {:pad 5}
                             :table/rows scroll-pane-rows})]
                 {:actor (scroll-pane/create
                          {:actor table
                           :skin skin})
                  :width  (+ (actor/width table) 50)
                  :height (min (- scroll-pane-height 50)
                               (actor/height table))})]]]
    (window/create
     {:title "[SKY]Property[]"
      :skin skin
      :window/close-button? skin
      :window/modal? true
      :table/cell-defaults {:pad 5}
      :table/rows rows
      :group/actors actors
      :actor/name "moon.ui.editor.window"})))
