# Plan

All tracks that can be executed.

## Nodes track: done

- [x] Struct works at node level
- [x] constraints define all the constraints the relations have between the nodes
- [x] relations is about querying the relations between nodes

## Operations track

- [x] raw-operation is an unchecked but bilateral on/off switch for the relations
- [x] raw-validation checks what raw operation is possible and what not based on the constraints
- [ ] checked-operation is a safe operation on top of raw validation and operation
- [ ] smooth-operation is a combine removal operation level (e.g. set function result if function has already a result will replace the current function result)
- [ ] smart-operation does combined operations like extract-subfunction, inline function...

## Constraintgen track

(on hold)

- [ ] Operations and operator tries to abstract away

## Execution track
One execution endpoint that aggregates all results of what needs to be executed, maybe use separate namespace in which
to put everything that needs to be calculated

## Typecheck track

Create a typecheck algorithm using basic data types (flow like)

## Environment track:

Load basic functions like "if", basic data structure operations

## Editor track:

- [ ] Create an editor environment which loads a file and maps operations on it,
- [ ] Also allows navigation and context (hides IDs away from user).
- [ ] Allow to load a basic environment

## UI track

- [ ] Create a modelview generation of a file
- [ ] Create a modelview for full editor track
- [ ] Create an fn-fx ui that uses

## Ast Editor track

- [ ] Connect all tracks