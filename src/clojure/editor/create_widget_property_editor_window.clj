(ns clojure.editor.create-widget-property-editor-window
  (:require
            [clojure.table-set-opts :as table-set-opts]
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [clojure.ui.error-window :as error-window]
            [clojure.scene2d.actor.find-ancestor :refer [find-ancestor]]
            [clojure.set-ctx :as set-ctx]
            [com.badlogic.gdx.scenes.scene2d.stage :as stage]
            [clojure.throwable :as throwable]
            [clojure.ui.window.add-close-button :as add-close-button]
            [clojure.db :as db]
            [clojure.editor.create-widget :refer [create-widget]]
            [clojure.editor.widget-value :refer [widget-value]]
            [com.badlogic.gdx.scenes.scene2d.event :as event]
            [com.badlogic.gdx.scenes.scene2d.group :as group]
            [com.badlogic.gdx.input :as input]
            [gdl.input.keys :as input-keys]
            [clojure.scroll-pane-cell :as scroll-pane-cell]
            [clojure.type :refer [property->type]]
            [com.badlogic.gdx.scenes.scene2d.ui.table :as table]
            [com.badlogic.gdx.scenes.scene2d.ui.text-button :as text-button]
            [com.badlogic.gdx.scenes.scene2d.ui.window :as window]
            [com.badlogic.gdx.scenes.scene2d.utils.change-listener :as change-listener]
            [com.badlogic.gdx.utils.viewport.viewport :as viewport]
            [com.badlogic.gdx.scenes.scene2d.ui.window :as gdx-window]))

(defn with-window-close [f]
  (fn [actor {:keys [ctx/skin
                     ctx/stage]
              :as ctx}]
    (try
     (let [new-ctx (update ctx :ctx/db f)
           stage (actor/getStage actor)]
       (set-ctx/f stage new-ctx))
     (actor/remove (find-ancestor actor (partial instance? gdx-window/class)))
     (catch Throwable t
       (throwable/pretty-pst t)
       (stage/addActor stage
                         (error-window/create
                          {:type :ui/error-window
                           :skin skin
                           :throwable t}))))))

(defn property-editor-window
  [{:keys [ctx
           property]}]
  (let [{:keys [ctx/db
                ctx/skin
                ctx/stage]} ctx
        schemas (:db/schemas db)
        schema (get schemas (property->type property))
        widget (create-widget schema property ctx)
        scroll-pane-height (viewport/getWorldHeight (:stage/viewport stage))
        get-widget-value #(widget-value schema widget schemas)
        property-id (:property/id property)
        clicked-delete-fn (with-window-close (fn [db]
                                               (db/delete! db property-id)))
        clicked-save-fn (with-window-close (fn [db]
                                             (db/update! db (get-widget-value))))
        scroll-pane-rows [[{:actor widget :colspan 2}]
                          [{:actor (doto (text-button/new "Save [LIGHT_GRAY](ENTER)[]" skin)
                                     (actor/addListener (change-listener/create
                                                              (fn [event actor]
                                                                (clicked-save-fn actor (:stage/ctx (event/getStage event)))))))
                            :center? true}
                           {:actor (doto (text-button/new "Delete" skin)
                                     (actor/addListener (change-listener/create
                                                              (fn [event actor]
                                                                (clicked-delete-fn actor (:stage/ctx (event/getStage event)))))))
                            :center? true}]]]
    (doto (doto (window/new "[SKY]Property[]" skin)
    (table-set-opts/set-opts! {:title "[SKY]Property[]"
            :skin skin
            :table/cell-defaults {:pad 5}
            :table/rows [[(scroll-pane-cell/create
                           (doto (table/new)
    (table-set-opts/set-opts! {:table/cell-defaults {:pad 5}
                                          :table/rows scroll-pane-rows}))
                           skin
                           scroll-pane-height
                           50)]]}))
      (add-close-button/f! skin)
      (gdx-window/setModal true)
      (group/addActor (actor/new
                       (fn [this _delta]
                         (when-let [stage (actor/getStage this)]
                           (let [ctx (:stage/ctx stage)]
                             (when (input/isKeyJustPressed (:ctx/input ctx)
                                                           (input-keys/key-to-value :input.keys/enter))
                               (clicked-save-fn this ctx)))))
                       (fn [_actor _batch _parent-alpha])))
      (actor/setName "moon.ui.clojure.editor-window"))))
