# Plan

All tracks that can be executed.

## Nodes track: done

1) Struct works at node level
2) constraints define all the constraints the relations have between the nodes
3) relations is about querying the relations between nodes

## Operations track

3) raw-operation is an unchecked but bilateral on/off switch for the relations
4) raw-validation checks what raw operation is possible and what not based on the constraints
5) checked-operation is a safe operation on top of raw validation and operation
6) smooth-operation is a combine removal operation level (e.g. set function result if function has already a result will replace the current function result)
7) smart-operation does combined operations like extract-subfunction, inline function...

## Constraintgen track

(on hold)

1) Operations and operator tries to abstract away

## Execution track
One execution endpoint that aggregates all results of what needs to be executed, maybe use separate namespace in which
to put everything that needs to be calculated

## Typecheck track

Create a typecheck algorithm using basic data types (flow like)

## Environment track:

Load basic functions like "if", basic data structure operations

## Editor track:

1) Create an editor environment which loads a file and maps operations on it,
2) Also allows navigation and context (hides IDs away from user).
3) Allow to load a basic environment

## UI track

1) Create a modelview generation of a file
2) Create a modelview for full editor track
3) Create an fn-fx ui that uses

